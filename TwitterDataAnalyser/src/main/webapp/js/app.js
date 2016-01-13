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
		desc : ' Analysts are rational and impartial. They are fiercely independent, open-minded, strong-willed and imaginative. These traits make Analysts excellent strategic thinkers, but also cause difficulties when it comes to social or romantic pursuits.',
		selected : false

	}, {
		name : 'Diplomat',
		desc : ' Diplomats focus on empathy and cooperation. They are imaginative, being harmonizers in their workplace or social circles. These traits make Diplomats warm, empathic and influential individuals, but also cause issues when there is a need to rely exclusively on cold rationality or make difficult decisions.',
		selected : false
	}, {
		name : 'Sentinel',
		desc : ' Sentinels are hard working, meticulous and traditional and create order, security and stability wherever they go. They stick to their plans and do not shy away from difficult tasks. However, they can also be very inflexible and reluctant to accept different points of view.',
		selected : false
	}, {
		name : 'Explorer',
		desc : ' Explorers are utilitarian and practical, spontaneous and shining in situations that require quick reaction and ability to think on your feet. They are masters in physical tools and techniques for convincing other people. They share the ability to connect with their surroundings & undertake risky endeavors or focus solely on sensual pleasures.',
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
