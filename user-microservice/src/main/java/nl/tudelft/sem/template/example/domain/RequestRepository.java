package nl.tudelft.sem.template.example.domain;


import feign.Param;
import java.util.List;
import nl.tudelft.sem.template.example.requests.UserRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface RequestRepository extends JpaRepository<UserRequest, Long> {
    List<UserRequest> findByUser(String user);
}
