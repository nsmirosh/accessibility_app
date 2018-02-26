package stuff.mykolamiroshnychenko.accessibilityapp.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ImageView

import java.util.ArrayDeque

import kotlin.mykolamiroshnychenko.accessibilityapp.R
import timber.log.Timber

class WidgetAccessibilityService : AccessibilityService() {

    private var mWindowManager: WindowManager? = null
    private var widgetView: View? = null
    internal var params: WindowManager.LayoutParams? = null

    private val listener = object : View.OnTouchListener {
        private var lastAction: Int = 0
        private var initialX: Int = 0
        private var initialY: Int = 0
        private var initialTouchX: Float = 0.toFloat()
        private var initialTouchY: Float = 0.toFloat()


        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //remember the initial position.
                    initialX = params!!.x
                    initialY = params!!.y

                    //get the touch location
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY

                    lastAction = event.action
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    performScroll()
                    lastAction = event.action
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    //Calculate the X and Y coordinates of the view.
                    params!!.x = initialX + (event.rawX - initialTouchX).toInt()
                    params!!.y = initialY + (event.rawY - initialTouchY).toInt()

                    //Update the layout with new X & Y coordinate
                    mWindowManager!!.updateViewLayout(widgetView, params)
                    lastAction = event.action
                    return true
                }
            }
            return false
        }

    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {

    }

    override fun onInterrupt() {

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Timber.d(" WidgetAccessibilityService onServiceConnected")
        configureTheUI()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun configureTheUI() {
        setUpWindowParams()
        setUpWidgetInWindow()
        setUpInteractionListeners()
    }


    private fun setUpWidgetInWindow() {
        widgetView = LayoutInflater.from(this).inflate(R.layout.bubble_layout, null)
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(widgetView, params)
    }


    private fun setUpInteractionListeners() {
        val closeButton = widgetView!!.findViewById<View>(R.id.close_btn) as ImageView
        closeButton.setOnClickListener {
            stopSelf()
        }

        val chatHeadImage = widgetView!!.findViewById<View>(R.id.chat_head_profile_iv) as ImageView
        chatHeadImage.setOnTouchListener(listener)
    }


    private fun setUpWindowParams() {

        var flags = WindowManager.LayoutParams.TYPE_PHONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flags = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        //Add the view to the window.
        params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                flags,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)

        params!!.gravity = Gravity.TOP or Gravity.LEFT        //Initially view will be added to top-left corner
        params!!.x = 0
        params!!.y = 100
    }


    private fun performScroll() {
        val scrollable = findScrollableNode(rootInActiveWindow)
        scrollable?.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD.id)
    }

    private fun findScrollableNode(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        val deque = ArrayDeque<AccessibilityNodeInfo>()
        deque.add(root)
        while (!deque.isEmpty()) {
            val node = deque.removeFirst()
            if (node.actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD)) {
                return node
            }
            for (i in 0 until node.childCount) {
                deque.addLast(node.getChild(i))
            }
        }
        return null
    }
}