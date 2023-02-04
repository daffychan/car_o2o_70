package cn.wolfcode.car.appointment.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.wolfcode.car.appointment.domain.info.ServiceItemAuditInfo;
import cn.wolfcode.car.appointment.domain.vo.BusAuditInfoVo;
import cn.wolfcode.car.appointment.domain.vo.BusServiceItemVo;
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
import cn.wolfcode.car.appointment.domain.BusServiceItem;
import cn.wolfcode.car.appointment.service.IBusServiceItemService;
import cn.wolfcode.car.common.utils.poi.ExcelUtil;
import cn.wolfcode.car.common.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 * 
 * @author wolfcode
 * @date 2022-12-30
 */
@RestController
@RequestMapping("/appointment/item")
public class BusServiceItemController extends BaseController
{
    @Autowired
    private IBusServiceItemService busServiceItemService;

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:item:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusServiceItem busServiceItem)
    {
        startPage();
        List<BusServiceItem> list = busServiceItemService.selectBusServiceItemList(busServiceItem);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:item:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusServiceItem busServiceItem)
    {
        List<BusServiceItem> list = busServiceItemService.selectBusServiceItemList(busServiceItem);
        ExcelUtil<BusServiceItem> util = new ExcelUtil<BusServiceItem>(BusServiceItem.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:item:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(busServiceItemService.selectBusServiceItemById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('appointment:item:add')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusServiceItemVo busServiceItemVo)
    {
        return toAjax(busServiceItemService.insertBusServiceItem(busServiceItemVo));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('appointment:item:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusServiceItemVo busServiceItemVo)
    {
        return toAjax(busServiceItemService.updateBusServiceItem(busServiceItemVo));
    }
    /**
     * 上架 saleOn
     */
    @PreAuthorize("@ss.hasPermi('appointment:item:saleOn')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping("/saleOn/{id}")
    public AjaxResult saleOn(@PathVariable Long id)
    {
        busServiceItemService.saleOn(id);
        return AjaxResult.success("服务项上架成功");
    }

    /**
     * 下架 saleOff
     */
    @PreAuthorize("@ss.hasPermi('appointment:item:saleOff')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping("/saleOff/{id}")
    public AjaxResult saleOff(@PathVariable Long id)
    {
        busServiceItemService.saleOff(id);
        return AjaxResult.success("服务项下架成功");
    }


    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('appointment:item:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(busServiceItemService.deleteBusServiceItemByIds(ids));
    }

    /**
     * @param id 服务项ID
     * @return json(serviceItem,店长列表,财务列表,流程定义价格)
     */
    @PreAuthorize("@ss.hasPermi('appointment:item:auditInfo')")
    @GetMapping("/auditInfo/{id}")
    public AjaxResult auditInfo(@PathVariable("id") Long id)
    {
        ServiceItemAuditInfo auditInfo = busServiceItemService.auditInfo(id);
        return AjaxResult.success(auditInfo);
    }

    /**
     * 发起服务项审核
     */
    @PreAuthorize("@ss.hasPermi('appointment:item:startAudit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping("/startAudit")
    public AjaxResult startAudit(@RequestBody BusAuditInfoVo busAuditInfoVo)
    {
        busServiceItemService.startAudit(busAuditInfoVo);
        return AjaxResult.success("发起审核成功");
    }
}
