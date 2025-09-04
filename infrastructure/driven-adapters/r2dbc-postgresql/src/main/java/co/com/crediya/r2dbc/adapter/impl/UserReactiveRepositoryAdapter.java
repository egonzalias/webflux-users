package co.com.crediya.r2dbc.adapter.impl;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.adapter.UserReactiveRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User, UserEntity, String, UserReactiveRepository
> implements UserRepository {

    private final TransactionalOperator transactionalOperator;

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class/* change for domain model */));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<Void> registerUser(User user) {
        UserEntity entity = mapper.map(user, UserEntity.class);
        return transactionalOperator.execute(tx -> repository.save(entity).then()).then();
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email).map(entity -> mapper.map(entity, User.class));
    }

    @Override
    public Mono<User> findByDocumentNumber(String document) {
        return repository.findByDocumentNumber(document).map(entity -> mapper.map(entity, User.class));
    }

}
