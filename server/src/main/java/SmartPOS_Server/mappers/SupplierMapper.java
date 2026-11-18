
package SmartPOS_Server.mappers;

import SmartPOS_Server.dtos.SupplierDTO;
import SmartPOS_Server.models.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public Supplier toEntity(SupplierDTO dto) {
        Supplier supplier = new Supplier();

        supplier.setName(dto.name());
        supplier.setContactPerson(dto.contactPerson());
        supplier.setPhone(dto.phone());
        supplier.setEmail(dto.email());
        supplier.setAddress(dto.address());
        supplier.setTaxId(dto.taxId());
        supplier.setNotes(dto.notes());

        return supplier;
    }

    public void updateEntity(Supplier supplier, SupplierDTO dto) {
        supplier.setName(dto.name());
        supplier.setContactPerson(dto.contactPerson());
        supplier.setPhone(dto.phone());
        supplier.setEmail(dto.email());
        supplier.setAddress(dto.address());
        supplier.setTaxId(dto.taxId());
        supplier.setNotes(dto.notes());
    }
}
