package nl.tudelft.sem.template.example.domain;

import nl.tudelft.sem.template.example.requests.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
interface RequestRepository extends JpaRepository<Request, Long> {

}
