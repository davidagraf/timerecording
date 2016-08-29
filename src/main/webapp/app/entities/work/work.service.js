(function() {
    'use strict';
    angular
        .module('timerecordingApp')
        .factory('Work', Work);

    Work.$inject = ['$resource', 'DateUtils'];

    function Work ($resource, DateUtils) {
        var resourceUrl =  'api/works/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.day = DateUtils.convertLocalDateFromServer(data.day);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.day = DateUtils.convertLocalDateToServer(data.day);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.day = DateUtils.convertLocalDateToServer(data.day);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
