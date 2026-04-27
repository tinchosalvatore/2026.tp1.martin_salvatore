package com.bibliotech.repository;

import com.bibliotech.db.JsonDatabase;
import com.bibliotech.model.Identifiable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class JsonRepository<T extends Identifiable<ID>, ID> implements Repository<T, ID> {
    private final JsonDatabase db;
    private final Supplier<List<T>> listSupplier;

    public JsonRepository(JsonDatabase db, Supplier<List<T>> listSupplier) {
        this.db = db;
        this.listSupplier = listSupplier;
    }

    @Override
    public void save(T entity) {
        List<T> list = listSupplier.get();
        list.removeIf(item -> item.id().equals(entity.id()));
        list.add(entity);
        db.save();
    }

    @Override
    public Optional<T> findById(ID id) {
        return listSupplier.get().stream()
                .filter(item -> item.id().equals(id))
                .findFirst();
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(listSupplier.get());
    }
}
