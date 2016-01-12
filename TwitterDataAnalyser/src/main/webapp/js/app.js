var app = angular.module('surveyApp', []);
var baseURL = "/TwitterDataAnalyser/mmds/";
var getTweetsURL = baseURL + "getTweetsForUser/";
var submitSurveyURL = baseURL + "submitSurvey/";

app.controller('mainController', function($scope, $http) {
	$scope.hideSurvey = false;
	$http.get(getTweetsURL).then(function(response) {
		$scope.userData = response.data;
	});
	$scope.personalityTypes = [ {
		name : 'Analyst',
		desc : ' Analysts embrace rationality and impartiality, excelling in intellectual debates. They are fiercely independent, open-minded, strong-willed and imaginative, approaching many things from a utilitarian perspective and being far more interested in what works than what satisfies everybody. These traits make Analysts excellent strategic thinkers, but also cause difficulties when it comes to social or romantic pursuits.',
		selected : false

	}, {
		name : 'Diplomat',
		desc : ' Diplomats focus on empathy and cooperation, shining in counselling. They are cooperative and imaginative, often playing the role of harmonizers in their workplace or social circles. These traits make Diplomats warm, empathic and influential individuals, but also cause issues when there is a need to rely exclusively on cold rationality or make difficult decisions.',
		selected : false
	}, {
		name : 'Sentinel',
		desc : ' Sentinels are cooperative and highly practical, embracing and creating order, security and stability wherever they go. They tend to be hard working, meticulous and traditional. They stick to their plans and do not shy away from difficult tasks. However, they can also be very inflexible and reluctant to accept different points of view.',
		selected : false
	}, {
		name : 'Explorer',
		desc : ' Explorers are utilitarian and practical, spontaneous and shining in situations that require quick reaction and ability to think on your feet. They are masters of tools and techniques, using them in many different ways – ranging from mastering physical tools to convincing other people. They share the ability to connect with their surroundings & their traits can also push them towards undertaking risky endeavors or focusing solely on sensual pleasures.',
		selected : false
	} ];
	$scope.selection = {
		personalities : {}
	};
	$scope.submit = function() {
		var selectedValues = [];
		angular.forEach($scope.personalityTypes, function(personality) {
			if (personality.selected == true) {
				selectedValues.push(personality.name);
			}
		});
		if (selectedValues.length == 0) {
			$scope.noSelectionErrorMsg = "Please select at least one option";
		} else {
			var data = {
				userId : $scope.userData.id,
				surveyResults : selectedValues
			};
			$http.post(submitSurveyURL, JSON.stringify(data));
			$scope.hideSurvey = true;
		}
	};
});
