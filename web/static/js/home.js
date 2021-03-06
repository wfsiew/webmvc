function HomeCtrl($scope, $http) {
	
	$scope.model = {
		id: '',
		useremail: ''
	};
	
	$scope.submit = function() {
		var o = {
			age: $scope.model.age,
			name: $scope.model.name,
			dob: '2015-07-01'
		};
		
		var cfg = utils.csrfHeader();
		
		$http.post('/webmvc/add/person', o, cfg).success(function(data) {
			for (var i in data) {
				alert(i + ' ' + data[i]);
			}
		});
	}
	
	$scope.submit2 = function() {
		var o = [1,2,3];
		var cfg = utils.csrfHeader();
		
		$http.post('/webmvc/list', o, cfg).success(function(data) {
			alert(data);
		});
	}
}

app.controller('HomeCtrl', ['$scope', '$http', HomeCtrl]);