package nl.tudelft.sem.template.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import nl.tudelft.sem.template.example.requests.Request;

@Repository
interface RequestRepository extends JpaRepository<Request, Long> {

}
