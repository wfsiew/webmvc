function HomeCtrl($scope, $http) {
	
	$scope.model = {
		id: '',
		useremail: ''
	};
	
	$scope.submit = function() {
		var o = {
			id: $scope.model.id,
			userEmail: $scope.model.useremail
		};
		
		$http.post('/webmvc/add', o).success(function(data) {
			alert(data.id + ", " + data.useremail);
		});
	}
}

app.controller('HomeCtrl', ['$scope', '$http', HomeCtrl]);