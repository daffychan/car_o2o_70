package cn.wolfcode.car.appointment.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.wolfcode.car.appointment.domain.vo.BusStatementVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.wolfcode.car.common.annotation.Log;
import cn.wolfcode.car.common.core.controller.BaseController;
import cn.wolfcode.car.common.core.domain.AjaxResult;
import cn.wolfcode.car.common.enums.BusinessType;
import cn.wolfcode.car.appointment.domain.BusStatement;
import cn.wolfcode.car.appointment.service.IBusStatementService;
import cn.wolfcode.car.common.utils.poi.ExcelUtil;
import cn.wolfcode.car.common.core.page.TableDataInfo;

/**
 * 结算单Controller
 * 
 * @author wolfcode
 * @date 2023-01-02
 */
@RestController
@RequestMapping("/appointment/statement")
public class BusStatementController extends BaseController
{
    @Autowired
    private IBusStatementService busStatementService;

    /**
     * 查询结算单列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusStatement busStatement)
    {
        startPage();
        List<BusStatement> list = busStatementService.selectBusStatementList(busStatement);
        return getDataTable(list);
    }

    /**
     * 导出结算单列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:export')")
    @Log(title = "结算单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusStatement busStatement)
    {
        List<BusStatement> list = busStatementService.selectBusStatementList(busStatement);
        ExcelUtil<BusStatement> util = new ExcelUtil<BusStatement>(BusStatement.class);
        util.exportExcel(response, list, "结算单数据");
    }

    /**
     * 获取结算单详细信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(busStatementService.selectBusStatementById(id));
    }

    /**
     * 新增结算单
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:add')")
    @Log(title = "结算单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusStatementVo busStatementVo)
    {
        return toAjax(busStatementService.insertBusStatement(busStatementVo));
    }


    /**
     * 修改结算单
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:edit')")
    @Log(title = "结算单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusStatementVo busStatementVo)
    {
        return toAjax(busStatementService.updateBusStatement(busStatementVo));
    }

    /**
     * 删除结算单
     */
    @PreAuthorize("@ss.hasPermi('appointment:statement:remove')")
    @Log(title = "结算单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(busStatementService.deleteBusStatementById(id));
    }
}
