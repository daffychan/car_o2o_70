package cn.wolfcode.car.appointment.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.wolfcode.car.appointment.domain.vo.BusAppointmentVo;
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
import cn.wolfcode.car.appointment.domain.BusAppointment;
import cn.wolfcode.car.appointment.service.IBusAppointmentService;
import cn.wolfcode.car.common.utils.poi.ExcelUtil;
import cn.wolfcode.car.common.core.page.TableDataInfo;

/**
 * 养修预约信息Controller
 * 
 * @author wolfcode
 * @date 2022-12-28
 */
@RestController
@RequestMapping("/appointment/appointment")
public class BusAppointmentController extends BaseController
{
    @Autowired
    private IBusAppointmentService busAppointmentService;

    /**
     * 查询养修预约信息列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:appointment:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusAppointment busAppointment)
    {
        startPage();
        List<BusAppointment> list = busAppointmentService.selectBusAppointmentList(busAppointment);
        return getDataTable(list);
    }

    /**
     * 导出养修预约信息列表
     */
    @PreAuthorize("@ss.hasPermi('appointment:appointment:export')")
    @Log(title = "养修预约信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusAppointment busAppointment)
    {
        List<BusAppointment> list = busAppointmentService.selectBusAppointmentList(busAppointment);
        ExcelUtil<BusAppointment> util = new ExcelUtil<BusAppointment>(BusAppointment.class);
        util.exportExcel(response, list, "养修预约信息数据");
    }

    /**
     * 获取养修预约信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:appointment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(busAppointmentService.selectBusAppointmentById(id));
    }

    /**
     * 新增养修预约信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:appointment:add')")
    @Log(title = "养修预约信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusAppointmentVo busAppointmentVo)
    {
        return toAjax(busAppointmentService.insertBusAppointment(busAppointmentVo));
    }

    /**
     * 修改养修预约信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:appointment:edit')")
    @Log(title = "养修预约信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusAppointmentVo busAppointmentVo)
    {
        return toAjax(busAppointmentService.updateBusAppointment(busAppointmentVo));
    }

    /**
     * 删除养修预约信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:appointment:remove')")
    @Log(title = "养修预约信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(busAppointmentService.deleteBusAppointmentByIds(ids));
    }
    /**
     * 到店
     * 更改预约信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:appointment:arrive')")
    @Log(title = "养修预约信息", businessType = BusinessType.UPDATE)
    @PutMapping("/arrival/{id}")
    public AjaxResult arrive(@PathVariable Long id){
        busAppointmentService.arrive(id);
        return AjaxResult.success("到店成功");
    }
    /**
     * 取消预约
     * 更改预约信息
     */
    @PreAuthorize("@ss.hasPermi('appointment:appointment:cancel')")
    @Log(title = "养修预约信息", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{id}")
    public AjaxResult cancel(@PathVariable Long id){
        busAppointmentService.cancel(id);
        return AjaxResult.success("取消成功");
    }
    /**
     * 预约用户生成结算单
     */
    @PreAuthorize("@ss.hasPermi('appointment:appointment:generate')")
    @Log(title = "结算单", businessType = BusinessType.INSERT)
    @PostMapping("/generate/{id}")
    public AjaxResult generate(@PathVariable Long id)
    {
        // 需要返回一个结算单ID
        return AjaxResult.success(busAppointmentService.generateBusStatement(id));
    }
}
