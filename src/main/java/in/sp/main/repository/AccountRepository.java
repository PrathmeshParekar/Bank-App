package in.sp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.sp.main.Entity.Account;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long>{
	Optional<Account> findByUsername(String username);



}
