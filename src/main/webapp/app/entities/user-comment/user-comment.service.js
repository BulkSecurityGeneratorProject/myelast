(function() {
    'use strict';
    angular
        .module('myelastApp')
        .factory('User_comment', User_comment);

    User_comment.$inject = ['$resource'];

    function User_comment ($resource) {
        var resourceUrl =  'api/user-comments/:id';

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
