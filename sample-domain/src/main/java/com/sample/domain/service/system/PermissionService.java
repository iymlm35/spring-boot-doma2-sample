package com.sample.domain.service.system;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.sample.domain.dto.common.Page;
import com.sample.domain.dto.common.Pageable;
import com.sample.domain.dto.system.Permission;
import com.sample.domain.dto.system.PermissionCriteria;
import com.sample.domain.repository.system.PermissionRepository;
import com.sample.domain.service.BaseTransactionalService;

/**
 * 権限サービス
 */
@Service
public class PermissionService extends BaseTransactionalService {

    @Autowired
    PermissionRepository permissionRepository;

    /**
     * 権限を複数取得します。
     *
     * @param criteria
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true) // 読み取りのみの場合は指定する
    public Page<Permission> findAll(PermissionCriteria criteria, Pageable pageable) {
        Assert.notNull(criteria, "criteria must not be null");
        return permissionRepository.findAll(criteria, pageable);
    }

    /**
     * 権限を取得します。
     *
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<Permission> findOne(PermissionCriteria criteria) {
        Assert.notNull(criteria, "criteria must not be null");
        return permissionRepository.findOne(criteria);
    }

    /**
     * 権限を取得します。
     *
     * @return
     */
    @Transactional(readOnly = true)
    public Permission findById(final Long id) {
        Assert.notNull(id, "id must not be null");
        return permissionRepository.findById(id);
    }

    /**
     * 権限を追加します。
     *
     * @param inputPermission
     * @return
     */
    public Permission create(final Permission inputPermission) {
        Assert.notNull(inputPermission, "inputPermission must not be null");
        return permissionRepository.create(inputPermission);
    }

    /**
     * 権限を更新します。
     *
     * @param inputPermission
     * @return
     */
    public Permission update(final Permission inputPermission) {
        Assert.notNull(inputPermission, "inputPermission must not be null");
        return permissionRepository.update(inputPermission);
    }

    /**
     * 権限を論理削除します。
     *
     * @return
     */
    public Permission delete(final Long id) {
        Assert.notNull(id, "id must not be null");
        return permissionRepository.delete(id);
    }
}
