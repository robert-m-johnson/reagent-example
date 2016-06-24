angular.module('app').controller("MainController", function() {
    var vm = this;
    vm.frameworks = [
        { name: "Angular", stars: 43, forks: 15 },
        { name: "React", stars: 34, forks: 9 }
    ];
});
