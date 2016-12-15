(function() {
    'use strict';

    angular
        .module('myelastApp')
        .controller('User_commentDetailController', User_commentDetailController);

    User_commentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'User_comment'];

    function User_commentDetailController($scope, $rootScope, $stateParams, previousState, entity, User_comment) {
        var vm = this;

        vm.user_comment = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('myelastApp:user_commentUpdate', function(event, result) {
            vm.user_comment = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
