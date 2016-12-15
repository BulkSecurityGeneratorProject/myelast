(function() {
    'use strict';

    angular
        .module('myelastApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-comment', {
            parent: 'entity',
            url: '/user-comment?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'User_comments'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-comment/user-comments.html',
                    controller: 'User_commentController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('user-comment-detail', {
            parent: 'entity',
            url: '/user-comment/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'User_comment'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-comment/user-comment-detail.html',
                    controller: 'User_commentDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'User_comment', function($stateParams, User_comment) {
                    return User_comment.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'user-comment',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('user-comment-detail.edit', {
            parent: 'user-comment-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-comment/user-comment-dialog.html',
                    controller: 'User_commentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['User_comment', function(User_comment) {
                            return User_comment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-comment.new', {
            parent: 'user-comment',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-comment/user-comment-dialog.html',
                    controller: 'User_commentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                comment: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-comment', null, { reload: 'user-comment' });
                }, function() {
                    $state.go('user-comment');
                });
            }]
        })
        .state('user-comment.edit', {
            parent: 'user-comment',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-comment/user-comment-dialog.html',
                    controller: 'User_commentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['User_comment', function(User_comment) {
                            return User_comment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-comment', null, { reload: 'user-comment' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-comment.delete', {
            parent: 'user-comment',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-comment/user-comment-delete-dialog.html',
                    controller: 'User_commentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['User_comment', function(User_comment) {
                            return User_comment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-comment', null, { reload: 'user-comment' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
