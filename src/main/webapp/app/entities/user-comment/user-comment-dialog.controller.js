(function() {
    'use strict';

    angular
        .module('myelastApp')
        .controller('User_commentDialogController', User_commentDialogController);

    User_commentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'User_comment'];

    function User_commentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, User_comment) {
        var vm = this;

        vm.user_comment = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.user_comment.id !== null) {
                User_comment.update(vm.user_comment, onSaveSuccess, onSaveError);
            } else {
                User_comment.save(vm.user_comment, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('myelastApp:user_commentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
