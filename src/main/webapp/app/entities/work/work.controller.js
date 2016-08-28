(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .controller('WorkController', WorkController);

    WorkController.$inject = ['$scope', '$state', 'Work'];

    function WorkController ($scope, $state, Work) {
        var vm = this;
        
        vm.works = [];

        loadAll();

        function loadAll() {
            Work.query(function(result) {
                vm.works = result;
            });
        }
    }
})();
