package br.com.jeovabezerraleite.todolist.repositories;

import br.com.jeovabezerraleite.todolist.domain.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IUserRepository extends JpaRepository<UserModel, UUID> {
    UserModel findByUserName(String userName);

}
