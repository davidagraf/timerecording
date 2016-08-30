(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .controller('EffortDialogController', EffortDialogController);

    EffortDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Effort', 'Project', 'User'];

    function EffortDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Effort, Project, User) {
        var vm = this;

        vm.effort = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
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
            if (vm.effort.id !== null) {
                Effort.update(vm.effort, onSaveSuccess, onSaveError);
            } else {
                Effort.save(vm.effort, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('timerecordingApp:effortUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.day = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
