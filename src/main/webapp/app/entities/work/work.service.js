(function() {
    'use strict';
    angular
        .module('timerecordingApp')
        .factory('Work', Work);

    Work.$inject = ['$resource'];

    function Work ($resource) {
        var resourceUrl =  'api/works/:id';

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
