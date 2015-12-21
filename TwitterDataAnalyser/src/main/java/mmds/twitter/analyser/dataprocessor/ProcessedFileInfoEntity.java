package mmds.twitter.analyser.dataprocessor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class ProcessedFileInfoEntity {

	@Id
	private String fileName;

	@Column
	@Enumerated(EnumType.STRING)
	private FileStatus status;

	public ProcessedFileInfoEntity() {
	}

	public ProcessedFileInfoEntity(String fileName, FileStatus fileStatus) {
		this.fileName = fileName;
		this.status = fileStatus;
	}

	static enum FileStatus {
		SUCCESS, FAILED
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}

}
