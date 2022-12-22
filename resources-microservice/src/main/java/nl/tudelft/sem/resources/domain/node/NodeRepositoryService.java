package nl.tudelft.sem.resources.domain.node;

import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.resources.database.NodeRepository;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class NodeRepositoryService {
    private final transient NodeRepository nodeRepository;

    @Autowired
    public NodeRepositoryService(NodeRepository nodeRepository){
        this.nodeRepository = nodeRepository;
    }

    public ResourceNode addNode(Node node, String netId) throws NameAlreadyInUseException {
        if(nodeRepository.existsByName(node.getName())) {
            throw new NameAlreadyInUseException(node.getName());
        }
        ResourceNode resourceNode = new ResourceNode(node.getToken(), node.getName(),
                new ResourcesDatabaseModel(node.getResources()), node.getUrl().toString(), netId);
        nodeRepository.save(resourceNode);
        return resourceNode;
    }

    public Collection<ResourceNode> getAllNodes() {
        return nodeRepository.findAll();
    }

    public void takeDownNodeOn(String name, LocalDate date){
        if(!nodeRepository.existsByName(name))
            return;
        ResourceNode node = nodeRepository.getOne(name);
        node.setTakeOfflineOn(date);
        nodeRepository.save(node);
    }

    public void clearAllExpiredNodes(LocalDate currentDate) {
        nodeRepository.deleteAll(nodeRepository.findAllByTakeOfflineOnIsLessThanEqual(currentDate));
    };
}
