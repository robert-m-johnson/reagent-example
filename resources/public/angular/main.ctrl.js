angular.module('app').controller("MainController", function($http, $interval) {
    var vm = this;

    vm.frameworks = [];

    var fetchData = function () {
        $http.get('/frameworks')
            .success(function(data, status, headers, config) {
                vm.frameworks = data;
            });
    };

    var interval = $interval(function () {
        fetchData();
    }, 3000);

    vm.endLongPolling = function () { $interval.cancel(interval); };

    fetchData();
});
