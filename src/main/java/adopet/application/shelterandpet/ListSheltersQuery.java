package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.Shelter;
import adopet.gateway.ShelterGateway;

import java.util.List;

public class ListSheltersQuery {

    private final ShelterGateway shelterGateway;

    public ListSheltersQuery(ShelterGateway shelterGateway) {
        this.shelterGateway = shelterGateway;
    }

    public List<Shelter> execute() {
        return shelterGateway.listShelters();
    }
}