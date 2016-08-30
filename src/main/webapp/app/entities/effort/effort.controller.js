(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .controller('EffortController', EffortController);

    EffortController.$inject = ['$scope', '$state', 'Effort'];

    function EffortController ($scope, $state, Effort) {
        var vm = this;
        
        vm.efforts = [];

        loadAll();

        function loadAll() {
            Effort.query(function(result) {
                vm.efforts = result;
            });
        }
    }
})();
