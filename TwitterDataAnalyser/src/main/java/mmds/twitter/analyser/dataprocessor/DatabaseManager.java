/*
 * Copyright (c) 2015, Cagiris Pvt. Ltd.
 * All rights reserved.
 */
package mmds.twitter.analyser.dataprocessor;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author: ssnk
 */

public class DatabaseManager implements IDatabaseManager {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
	private static final String DEFAULT_HIBERNATE_CONGFIG_FILE_NAME = "hibernate.cfg.xml";
	private static final String HIBERNATE_CONNECTION_URL_PROPERTY = "connection.url";
	private static final String DB_EXIST_SQL_STATE = "42P04";
	private static final String HIBERNATE_USERNAME_PROPERTY = "connection.username";
	private static final String JDBC_USERNAME_PROPERTY = "user";
	private static final String HIBERNATE_PASSWORD_PROPERTY = "connection.password";
	private static final String JDBC_PASSWORD_PROPERTY = "password";
	private static final String DB_DRIVER_CLASS_NAME = "connection.driver_class";

	private SessionFactory sessionFactory;

	private List<String> classesToAdd = new ArrayList<>();

	private Set<Class<?>> entityClasses = new HashSet<>();

	public void init() throws DatabaseManagerException {
		String configFileName = DEFAULT_HIBERNATE_CONGFIG_FILE_NAME;

		logger.info("Using hibernate config file:{}", configFileName);

		URL resourceURL = this.getClass().getClassLoader().getResource(configFileName);

		Configuration configuration = new Configuration().configure(resourceURL);

		String connectionURL = configuration.getProperty(HIBERNATE_CONNECTION_URL_PROPERTY);
		Properties properties = new Properties();
		properties.setProperty(JDBC_USERNAME_PROPERTY, configuration.getProperty(HIBERNATE_USERNAME_PROPERTY));
		properties.setProperty(JDBC_PASSWORD_PROPERTY, configuration.getProperty(HIBERNATE_PASSWORD_PROPERTY));
		properties.setProperty(DB_DRIVER_CLASS_NAME, configuration.getProperty(DB_DRIVER_CLASS_NAME));
		createDBIfNotPresent(connectionURL, properties);
		for (String clazz : classesToAdd) {
			Class<?> loadClass;
			try {
				loadClass = this.getClass().getClassLoader().loadClass(clazz);
				entityClasses.add(loadClass);
			} catch (ClassNotFoundException e) {
				logger.error("Error while loading class:{}", clazz, e);
			}
		}
		for (Class<?> clazz : entityClasses) {
			configuration.addAnnotatedClass(clazz);
		}
		configuration.setNamingStrategy(new ImprovedNamingStrategy());
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(configuration.getProperties());
		this.sessionFactory = configuration.buildSessionFactory(builder.build());
	}

	private void createDBIfNotPresent(String dbURL, Properties properties) throws DatabaseManagerException {
		URI dbURI;
		try {
			dbURI = new URI(dbURL.substring(5));
		} catch (URISyntaxException e) {
			logger.error("Invalid db url:{}", dbURL);
			throw new DatabaseManagerException(e.getMessage(), e);
		}
		String host = dbURI.getHost();
		String dbName = dbURI.getPath();
		dbName = dbName.replace("/", "");
		int port = dbURI.getPort();
		String connectionURL = "jdbc:" + dbURI.getScheme() + "://" + host;
		if (port != -1) {
			connectionURL += ":" + port;
		}
		logger.info("Database name: {}, Connection URL: {}", dbName, connectionURL);

		String dbDriverClazz = properties.getProperty(DB_DRIVER_CLASS_NAME);
		try {
			Class.forName(dbDriverClazz);
		} catch (ClassNotFoundException e) {
			logger.error("Failed to load driver: {}", dbDriverClazz, e);
			throw new DatabaseManagerException(e.getMessage(), e);
		}

		try (Connection connection = DriverManager.getConnection(connectionURL, properties)) {
			try (Statement createStatement = connection.createStatement()) {
				createStatement.executeUpdate("Create database " + dbName);
			} catch (SQLException e) {
				if (DB_EXIST_SQL_STATE.equals(e.getSQLState())) {
					logger.info("DB already exists: {}", dbName);
				} else {
					logger.error("Error while creating db:{}", e.getMessage());
					throw new DatabaseManagerException(e);
				}
			}
		} catch (SQLException e) {
			logger.error("Error while creating db connection:{}", e.getMessage());
			throw new DatabaseManagerException(e.getMessage(), e);
		}
	}

	public Serializable save(Object entity) throws DatabaseManagerException {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Serializable save = session.save(entity);
			tx.commit();
			return save;
		} catch (HibernateException e) {
			logger.error("Error while executing save", e);
			rollbackTransaction(tx);
			throw new DatabaseManagerException(e);
		} finally {
			closeSession(session);
		}
	}

	private void rollbackTransaction(Transaction tx) {
		if (tx != null) {
			try {
				tx.rollback();
			} catch (HibernateException e1) {
				logger.error("Error during rollback : {}", e1.getMessage(), e1);
			}
		}
	}

	private void closeSession(Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (HibernateException e) {
				logger.error("Error while closing session: {}", e.getMessage(), e);
			}
		}
	}

	@Override
	public Serializable save(String entityName, Object entity) throws DatabaseManagerException {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Serializable save = session.save(entityName, entity);
			tx.commit();
			return save;
		} catch (HibernateException e) {
			logger.error("Error while executing save", e);
			rollbackTransaction(tx);
			throw new DatabaseManagerException(e);
		} finally {
			closeSession(session);
		}
	}

	@Override
	public void update(Object entity) throws DatabaseManagerException {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.update(entity);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error while executing update", e);
			rollbackTransaction(tx);
			throw new DatabaseManagerException(e);
		} finally {
			closeSession(session);
		}
	}

	@Override
	public void update(String entityName, Object entity) throws DatabaseManagerException {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.update(entityName, entity);
			tx.commit();
		} catch (HibernateException e) {
			rollbackTransaction(tx);
			throw new DatabaseManagerException(e);
		} finally {
			closeSession(session);
		}
	}

	@Override
	public void saveOrUpdate(Object entity) throws DatabaseManagerException {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(entity);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error while executing save/update", e);
			rollbackTransaction(tx);
			throw new DatabaseManagerException(e);
		} finally {
			closeSession(session);
		}
	}

	@Override
	public void saveOrUpdate(String entityName, Object entity) throws DatabaseManagerException {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(entityName, entity);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error while executing save/update", e);
			rollbackTransaction(tx);
			throw new DatabaseManagerException(e);
		} finally {
			closeSession(session);
		}
	}

	@Override
	public <T> T get(Class<T> entityClass, Serializable id) throws DatabaseManagerException, EntityNotFoundException {
		Session session = sessionFactory.openSession();
		try {
			T entity = (T) session.get(entityClass, id);
			checkIfNotNull(entityClass.getClass().getName(), id, entity);
			return entity;
		} catch (HibernateException e) {
			logger.error("Error while getting for id:{}", id, e);
			throw new DatabaseManagerException(e);
		} finally {
			closeSession(session);
		}
	}

	private <T> void checkIfNotNull(String className, Serializable id, T entity) throws EntityNotFoundException {
		if (entity == null) {
			String message = "Entity " + className + " with id " + id + " not found";
			throw new EntityNotFoundException(message);
		}
	}

	@Override
	public Object get(String entityName, Serializable id) throws DatabaseManagerException, EntityNotFoundException {
		Session session = sessionFactory.openSession();
		try {
			Object entity = session.get(entityName, id);
			checkIfNotNull(entityName, id, entity);
			return entity;
		} catch (HibernateException e) {
			logger.error("Error while getting for id:{}", id, e);
			throw new DatabaseManagerException(e);
		} finally {
			closeSession(session);
		}
	}

	@Override
	public SessionFactory getSessionFactory() throws DatabaseManagerException {
		return this.sessionFactory;
	}

	@Override
	public void delete(Object entity) throws DatabaseManagerException {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(entity);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error while deleting entity", e);
			rollbackTransaction(tx);
			throw new DatabaseManagerException(e);
		} finally {
			closeSession(session);
		}
	}

	@Override
	public void delete(String entityName, Object entity) throws DatabaseManagerException {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(entityName, entity);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error while deleting entity", e);
			rollbackTransaction(tx);
			throw new DatabaseManagerException(e);
		} finally {
			closeSession(session);
		}
	}

	@Override
	public void deleteAll(Collection<?> entities) throws DatabaseManagerException {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			for (Object entity : entities) {
				session.delete(entity);
			}
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error while deleting entities", e);
			rollbackTransaction(tx);
			throw new DatabaseManagerException(e);
		} finally {
			closeSession(session);
		}
	}

	public List<String> getClassesToAdd() {
		return classesToAdd;
	}

	public void setClassesToAdd(List<String> classesToAdd) {
		this.classesToAdd = classesToAdd;
	}

	public Set<Class<?>> getEntityClasses() {
		return entityClasses;
	}

	public void setEntityClasses(List<Class<?>> entityClasses) {
		this.entityClasses.addAll(entityClasses);
	}

}
