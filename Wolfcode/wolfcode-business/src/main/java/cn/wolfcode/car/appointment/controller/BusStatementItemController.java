package cn.wolfcode.car.appointment.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.wolfcode.car.appointment.domain.vo.BusStatementItemVo;
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
import cn.wolfcode.car.appointment.domain.BusStatementItem;
import cn.wolfcode.car.appointment.service.IBusStatementItemService;
import cn.wolfcode.car.common.utils.poi.ExcelUtil;
import cn.wolfcode.car.common.core.page.TableDataInfo;

/**
 * 明细单Controller
 * 
 * @author wolfcode
 * @date 2023-01-03
 */
@RestController
@RequestMapping("/appointment/statementitem")
public class BusStatementItemController extends BaseController
{
    @Autowired
    private IBusStatementItemService busStatementItemService;

    /**
     * 查询明细单列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:statementitem:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusStatementItem busStatementItem)
    {
        startPage();
        List<BusStatementItem> list = busStatementItemService.selectBusStatementItemList(busStatementItem);
        return getDataTable(list);
    }

    /**
     * 导出明细单列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:statementitem:export')")
    @Log(title = "明细单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusStatementItem busStatementItem)
    {
        List<BusStatementItem> list = busStatementItemService.selectBusStatementItemList(busStatementItem);
        ExcelUtil<BusStatementItem> util = new ExcelUtil<BusStatementItem>(BusStatementItem.class);
        util.exportExcel(response, list, "明细单数据");
    }

    /**
     * 获取明细单详细信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:statementitem:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(busStatementItemService.selectBusStatementItemById(id));
    }

    /**
     * 新增明细单
     */
    @PreAuthorize("@ss.hasPermi('appointment:statementitem:add')")
    @Log(title = "明细单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusStatementItemVo busStatementItemVo)
    {
        busStatementItemService.saveBusStatementItems(busStatementItemVo);
        return AjaxResult.success("新增结算单明细成功");
    }

    /**
     * 支付
     */
    @PreAuthorize("@ss.hasPermi('appointment:statementitem:pay')")
    @Log(title = "明细单", businessType = BusinessType.INSERT)
    @PostMapping("/pay")
    public AjaxResult pay(@RequestBody BusStatementItemVo busStatementItemVo)
    {
        busStatementItemService.pay(busStatementItemVo);
        return AjaxResult.success("支付成功");
    }

    /**
     * 修改明细单
     */
    @PreAuthorize("@ss.hasPermi('appointment:statementitem:edit')")
    @Log(title = "明细单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusStatementItem busStatementItem)
    {
        return toAjax(busStatementItemService.updateBusStatementItem(busStatementItem));
    }

    /**
     * 删除明细单
     */
    @PreAuthorize("@ss.hasPermi('appointment:statementitem:remove')")
    @Log(title = "明细单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(busStatementItemService.deleteBusStatementItemByIds(ids));
    }
}
