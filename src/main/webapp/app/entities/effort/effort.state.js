(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('effort', {
            parent: 'entity',
            url: '/effort',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'timerecordingApp.effort.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/effort/efforts.html',
                    controller: 'EffortController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('effort');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('effort-detail', {
            parent: 'entity',
            url: '/effort/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'timerecordingApp.effort.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/effort/effort-detail.html',
                    controller: 'EffortDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('effort');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Effort', function($stateParams, Effort) {
                    return Effort.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'effort',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('effort-detail.edit', {
            parent: 'effort-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/effort/effort-dialog.html',
                    controller: 'EffortDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Effort', function(Effort) {
                            return Effort.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('effort.new', {
            parent: 'effort',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/effort/effort-dialog.html',
                    controller: 'EffortDialogController',
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
                    $state.go('effort', null, { reload: 'effort' });
                }, function() {
                    $state.go('effort');
                });
            }]
        })
        .state('effort.edit', {
            parent: 'effort',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/effort/effort-dialog.html',
                    controller: 'EffortDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Effort', function(Effort) {
                            return Effort.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('effort', null, { reload: 'effort' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('effort.delete', {
            parent: 'effort',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/effort/effort-delete-dialog.html',
                    controller: 'EffortDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Effort', function(Effort) {
                            return Effort.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('effort', null, { reload: 'effort' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
