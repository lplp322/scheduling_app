package nl.tudelft.sem.resources.database;

import nl.tudelft.sem.resources.domain.node.ResourceNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<ResourceNode, String> {

    boolean existsByName(String Name);

    List<ResourceNode> findAllByTakeOfflineOnIsLessThanEqual(LocalDate date);
}
