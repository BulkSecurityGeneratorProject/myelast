(function() {
    'use strict';

    angular
        .module('myelastApp')
        .factory('User_commentSearch', User_commentSearch);

    User_commentSearch.$inject = ['$resource'];

    function User_commentSearch($resource) {
        var resourceUrl =  'api/_search/user-comments/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
