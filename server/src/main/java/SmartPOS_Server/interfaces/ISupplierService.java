
package SmartPOS_Server.interfaces;

import SmartPOS_Server.dtos.SupplierDTO;
import SmartPOS_Server.models.Supplier;

import java.util.List;

public interface ISupplierService {

    List<Supplier> getAllSuppliers();

    Supplier getSupplierById(String id);

    Supplier addSupplier(SupplierDTO supplierDTO);

    Supplier updateSupplier(String id, SupplierDTO supplierDTO);

    void deleteSupplier(String id);

    List<Supplier> searchSuppliers(String search);
}
