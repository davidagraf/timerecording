(function() {
    'use strict';
    angular
        .module('timerecordingApp')
        .factory('Expense', Expense);

    Expense.$inject = ['$resource'];

    function Expense ($resource) {
        var resourceUrl =  'api/expenses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
