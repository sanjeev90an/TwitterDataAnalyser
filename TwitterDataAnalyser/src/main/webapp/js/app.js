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
		name : 'Type A',
		desc : 'desc A',
		selected : false

	}, {
		name : 'Type B',
		desc : 'desc B',
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
