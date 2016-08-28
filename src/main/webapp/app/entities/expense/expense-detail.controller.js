(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .controller('ExpenseDetailController', ExpenseDetailController);

    ExpenseDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Expense', 'Project', 'User'];

    function ExpenseDetailController($scope, $rootScope, $stateParams, previousState, entity, Expense, Project, User) {
        var vm = this;

        vm.expense = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('timerecordingApp:expenseUpdate', function(event, result) {
            vm.expense = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
