(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('work', {
            parent: 'entity',
            url: '/work',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Works'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/work/works.html',
                    controller: 'WorkController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('work-detail', {
            parent: 'entity',
            url: '/work/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Work'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/work/work-detail.html',
                    controller: 'WorkDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Work', function($stateParams, Work) {
                    return Work.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'work',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('work-detail.edit', {
            parent: 'work-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/work/work-dialog.html',
                    controller: 'WorkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Work', function(Work) {
                            return Work.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('work.new', {
            parent: 'work',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/work/work-dialog.html',
                    controller: 'WorkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                hours: null,
                                day: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('work', null, { reload: 'work' });
                }, function() {
                    $state.go('work');
                });
            }]
        })
        .state('work.edit', {
            parent: 'work',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/work/work-dialog.html',
                    controller: 'WorkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Work', function(Work) {
                            return Work.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('work', null, { reload: 'work' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('work.delete', {
            parent: 'work',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/work/work-delete-dialog.html',
                    controller: 'WorkDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Work', function(Work) {
                            return Work.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('work', null, { reload: 'work' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
