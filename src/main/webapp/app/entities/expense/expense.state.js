(function() {
    'use strict';

    angular
        .module('timerecordingApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('expense', {
            parent: 'entity',
            url: '/expense',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Expenses'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/expense/expenses.html',
                    controller: 'ExpenseController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('expense-detail', {
            parent: 'entity',
            url: '/expense/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Expense'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/expense/expense-detail.html',
                    controller: 'ExpenseDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Expense', function($stateParams, Expense) {
                    return Expense.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'expense',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('expense-detail.edit', {
            parent: 'expense-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/expense/expense-dialog.html',
                    controller: 'ExpenseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Expense', function(Expense) {
                            return Expense.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('expense.new', {
            parent: 'expense',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/expense/expense-dialog.html',
                    controller: 'ExpenseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                description: null,
                                amount: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('expense', null, { reload: 'expense' });
                }, function() {
                    $state.go('expense');
                });
            }]
        })
        .state('expense.edit', {
            parent: 'expense',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/expense/expense-dialog.html',
                    controller: 'ExpenseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Expense', function(Expense) {
                            return Expense.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('expense', null, { reload: 'expense' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('expense.delete', {
            parent: 'expense',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/expense/expense-delete-dialog.html',
                    controller: 'ExpenseDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Expense', function(Expense) {
                            return Expense.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('expense', null, { reload: 'expense' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
