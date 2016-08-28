(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .controller('ExpenseDialogController', ExpenseDialogController);

    ExpenseDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Expense', 'Project', 'User'];

    function ExpenseDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Expense, Project, User) {
        var vm = this;

        vm.expense = entity;
        vm.clear = clear;
        vm.save = save;
        vm.projects = Project.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.expense.id !== null) {
                Expense.update(vm.expense, onSaveSuccess, onSaveError);
            } else {
                Expense.save(vm.expense, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('timerecordingApp:expenseUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
