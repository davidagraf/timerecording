(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .controller('WorkDialogController', WorkDialogController);

    WorkDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Work', 'Project', 'User'];

    function WorkDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Work, Project, User) {
        var vm = this;

        vm.work = entity;
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
            if (vm.work.id !== null) {
                Work.update(vm.work, onSaveSuccess, onSaveError);
            } else {
                Work.save(vm.work, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('timerecordingApp:workUpdate', result);
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
