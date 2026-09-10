package com.example.shopicaros.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopicaros.R
import com.example.shopicaros.data.model.User
import com.example.shopicaros.session.UserRole

class UsersAdapter :
    ListAdapter<
            User,
            UsersAdapter.UserViewHolder
            >(UserDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_user,
                    parent,
                    false
                )

        return UserViewHolder(
            view as ViewGroup
        )
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int
    ) {

        holder.bind(
            getItem(position)
        )
    }

    class UserViewHolder(
        root: ViewGroup
    ) : RecyclerView.ViewHolder(root) {

        private val tvUserName: TextView =
            root.findViewById(
                R.id.tvUserName
            )

        private val tvUsername: TextView =
            root.findViewById(
                R.id.tvUsername
            )

        private val tvEmail: TextView =
            root.findViewById(
                R.id.tvUserEmail
            )

        private val tvPhone: TextView =
            root.findViewById(
                R.id.tvUserPhone
            )

        private val tvRole: TextView =
            root.findViewById(
                R.id.tvUserRole
            )

        fun bind(
            user: User
        ) {

            tvUserName.text =
                if (user.fullName.isBlank()) {
                    "Sin nombre"
                } else {
                    user.fullName
                }

            tvUsername.text =
                "@${user.username}"

            tvEmail.text =
                if (user.email.isBlank()) {
                    "Correo no disponible"
                } else {
                    user.email
                }

            tvPhone.text =
                if (user.phone.isBlank()) {
                    "Teléfono no disponible"
                } else {
                    user.phone
                }

            tvRole.text =
                UserRole
                    .fromUserId(
                        user.id
                    )
                    .value
        }
    }

    private class UserDiffCallback :
        DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean {

            return oldItem.id ==
                    newItem.id
        }

        override fun areContentsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean {

            return oldItem ==
                    newItem
        }
    }
}