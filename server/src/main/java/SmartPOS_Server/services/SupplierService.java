
package SmartPOS_Server.services;

import SmartPOS_Server.dtos.SupplierDTO;
import SmartPOS_Server.exceptions.ResourceNotFoundException;
import SmartPOS_Server.interfaces.ISupplierService;
import SmartPOS_Server.mappers.SupplierMapper;
import SmartPOS_Server.models.Supplier;
import SmartPOS_Server.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService implements ISupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierService(
            SupplierRepository supplierRepository,
            SupplierMapper supplierMapper) {

        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier getSupplierById(String id) {
        return supplierRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("הספק לא נמצא"));
    }

    @Override
    public Supplier addSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier updateSupplier(String id, SupplierDTO supplierDTO) {
        Supplier existingSupplier = getSupplierById(id);

        supplierMapper.updateEntity(existingSupplier, supplierDTO);

        return supplierRepository.save(existingSupplier);
    }

    @Override
    public void deleteSupplier(String id) {
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }

    @Override
    public List<Supplier> searchSuppliers(String search) {
        if (search == null || search.isBlank()) {
            return getAllSuppliers();
        }

        String value = search.trim();

        return supplierRepository
                .findByNameContainingIgnoreCaseOrContactPersonContainingIgnoreCase(
                        value, value
                );
    }
}
