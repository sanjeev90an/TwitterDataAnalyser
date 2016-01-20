var app = angular.module('surveyApp', []);
var baseURL = "/TwitterDataAnalyser/mmds/";
var getTweetsURL = baseURL + "getTweetsForUser/";
var submitSurveyURL = baseURL + "submitSurvey/";

app.controller('mainController', function($scope, $http) {
	$scope.hideSurvey = false;
	$http.get(getTweetsURL).then(function(response) {
		$scope.userData = response.data;
	});
	$scope.personalityTypes =[
    {
        name:"Conscientiousness",
        desc:"Scrupulous, meticulous and principled behavior",
        selected:false
    },
    {
        name:"Extrovert",
        desc:"Gregarious, outgoing, sociable and projecting one's personality outward",
        selected:false
    },
    {
        name:"Agreeable",
        desc:"Compliant, trusting, friendly and cooperative nature",
        selected:false
    },
    {
        name:"Empathetic",
        desc:"Understands and shares the feelings of another",
        selected:false
    },
    {
        name:"Novelty Seeking",
        desc:"Exploratory, fickle, excitable, quick tempered and extravagant",
        selected:false
    },
    {
        name:"Perfectionist",
        desc:"Has an internally motivated desire to be perfect",
        selected:false
    },
    {
        name:"Rigid",
        desc:"Inflexibile, difficulty making transitions, adherence to set patterns",
        selected:false
    },
    {
        name:"Impulsive",
        desc:"Risk taking, lack of planning and making up one's mind quickly",
        selected:false
    },
    {
        name:"Psychopath",
        desc:"An unstable and aggressive person",
        selected:false
    },
    {
        name:"Obsessive",
        desc:"Associated with addictive behavior",
    }
];
    $scope.personalityDict = {};
    
	$scope.selection = {};
	$scope.submit = function() {
	    var selectedValues = [];
		if (!$scope.selection.name) {
			$scope.noSelectionErrorMsg = "Please select at least one option";
		} else {
    	    selectedValues.push($scope.selection.name)   ;
			var data = {
				userId : $scope.userData.id,
				surveyResults : selectedValues
			};
			$http.post(submitSurveyURL, JSON.stringify(data));
			$scope.hideSurvey = true;
		}
	};
});
