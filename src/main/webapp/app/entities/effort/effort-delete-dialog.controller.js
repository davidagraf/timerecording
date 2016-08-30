(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .controller('EffortDeleteController',EffortDeleteController);

    EffortDeleteController.$inject = ['$uibModalInstance', 'entity', 'Effort'];

    function EffortDeleteController($uibModalInstance, entity, Effort) {
        var vm = this;

        vm.effort = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Effort.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
