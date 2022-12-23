package nl.tudelft.sem.resources.domain.node;

import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.resources.database.NodeRepository;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class NodeRepositoryService {
    private final transient NodeRepository nodeRepository;

    @Autowired
    public NodeRepositoryService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    /** Adds a node to the repository.
     *
     * @param node node to be added
     * @param netId netId of the user who added the node
     * @return returns the node if added correctly
     * @throws NameAlreadyInUseException if the name of the node is already taken
     */
    public ResourceNode addNode(Node node, String netId) throws NameAlreadyInUseException {
        if (nodeRepository.existsByName(node.getName())) {
            throw new NameAlreadyInUseException(node.getName());
        }
        ResourceNode resourceNode = new ResourceNode(node.getToken(), node.getName(),
                new ResourcesDatabaseModel(node.getResources()), node.getUrl(), netId, node.getFaculty());
        return nodeRepository.save(resourceNode);
    }

    public Collection<Node> getAllNodes() {
        return nodeRepository.findAll().stream().map(a -> new Node(a.getName(), a.getUrl(), a.getToken(),
                a.getResources(), a.getFaculty())).collect(Collectors.toList());
    }

}
