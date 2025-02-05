package org.testing.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.testing.spring.dao.BancoDao;
import org.testing.spring.dao.CuentaDao;
import org.testing.spring.model.Banco;
import org.testing.spring.model.Cuenta;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CuentaServiceImpl implements CuentaService {


    private CuentaDao cuentaDao;
    private BancoDao bancoDao;

    public CuentaServiceImpl(CuentaDao cuentaDao, BancoDao bancoDao) {
        this.cuentaDao = cuentaDao;
        this.bancoDao = bancoDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> findAll() {
        return this.cuentaDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta findById(Long id) {
        return cuentaDao.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public Cuenta save(Cuenta cuenta) {
        return this.cuentaDao.save(cuenta);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        this.cuentaDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public int revisarTotalTransferencias(Long bancoId) {
        Banco banco = bancoDao.findById(bancoId).orElseThrow();
        return banco.getTotalTransferencia();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal revisarSaldo(Long cuentaId) {
        Cuenta cuenta = cuentaDao.findById(cuentaId).orElseThrow();
        return cuenta.getSaldo();
    }

    @Override
    @Transactional
    public void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId) {

        Cuenta cuentaOrigen = cuentaDao.findById(numCuentaOrigen).orElseThrow();
        cuentaOrigen.debito(monto);
        cuentaDao.save(cuentaOrigen);

        Cuenta cuentaDetino = cuentaDao.findById(numCuentaDestino).orElseThrow();
        cuentaDetino.credito(monto);
        cuentaDao.save(cuentaDetino);

        Banco banco = bancoDao.findById(bancoId).orElseThrow();

        int totalTranferencias = banco.getTotalTransferencia();
        banco.setTotalTransferencia(++totalTranferencias);
        bancoDao.save(banco);
    }


}
