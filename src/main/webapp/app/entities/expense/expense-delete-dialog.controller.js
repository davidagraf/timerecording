(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .controller('ExpenseDeleteController',ExpenseDeleteController);

    ExpenseDeleteController.$inject = ['$uibModalInstance', 'entity', 'Expense'];

    function ExpenseDeleteController($uibModalInstance, entity, Expense) {
        var vm = this;

        vm.expense = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Expense.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
