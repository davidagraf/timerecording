(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .controller('WorkDetailController', WorkDetailController);

    WorkDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Work', 'Project', 'User'];

    function WorkDetailController($scope, $rootScope, $stateParams, previousState, entity, Work, Project, User) {
        var vm = this;

        vm.work = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('timerecordingApp:workUpdate', function(event, result) {
            vm.work = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
