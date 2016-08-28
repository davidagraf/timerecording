(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .controller('ExpenseController', ExpenseController);

    ExpenseController.$inject = ['$scope', '$state', 'Expense'];

    function ExpenseController ($scope, $state, Expense) {
        var vm = this;
        
        vm.expenses = [];

        loadAll();

        function loadAll() {
            Expense.query(function(result) {
                vm.expenses = result;
            });
        }
    }
})();
