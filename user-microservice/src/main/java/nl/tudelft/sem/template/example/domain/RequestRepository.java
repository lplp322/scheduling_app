package nl.tudelft.sem.template.example.domain;


import nl.tudelft.sem.template.example.requests.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RequestRepository extends JpaRepository<UserRequest, Long> {

}
