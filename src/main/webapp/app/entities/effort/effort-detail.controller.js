(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .controller('EffortDetailController', EffortDetailController);

    EffortDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Effort', 'Project', 'User'];

    function EffortDetailController($scope, $rootScope, $stateParams, previousState, entity, Effort, Project, User) {
        var vm = this;

        vm.effort = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('timerecordingApp:effortUpdate', function(event, result) {
            vm.effort = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
