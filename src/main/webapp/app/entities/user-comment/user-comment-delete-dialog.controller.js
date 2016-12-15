(function() {
    'use strict';

    angular
        .module('myelastApp')
        .controller('User_commentDeleteController',User_commentDeleteController);

    User_commentDeleteController.$inject = ['$uibModalInstance', 'entity', 'User_comment'];

    function User_commentDeleteController($uibModalInstance, entity, User_comment) {
        var vm = this;

        vm.user_comment = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            User_comment.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
