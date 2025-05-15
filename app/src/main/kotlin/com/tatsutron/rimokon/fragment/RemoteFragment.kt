package com.tatsutron.rimokon.fragment

import android.lib.widget.verticalmarqueetextview.VerticalMarqueeTextView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.tatsutron.rimokon.R

class RemoteFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(
            R.layout.fragment_remote,
            container,
            false,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.up_button).apply {
            setOnClickListener {
            }
        }
        view.findViewById<Button>(R.id.down_button).apply {
            setOnClickListener {
            }
        }
        view.findViewById<Button>(R.id.left_button).apply {
            setOnClickListener {
            }
        }
        view.findViewById<Button>(R.id.right_button).apply {
            setOnClickListener {
            }
        }
        view.findViewById<Button>(R.id.escape_button).apply {
            setOnClickListener {
            }
        }
        view.findViewById<Button>(R.id.enter_button).apply {
            setOnClickListener {
            }
        }
    }
}
