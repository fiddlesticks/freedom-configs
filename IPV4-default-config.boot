firewall {
    all-ping enable
    broadcast-ping disable
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians enable
    ipv6-name WANv6_IN {
        default-action drop
        description "WAN inbound traffic forwarded to LAN"
        enable-default-log
        rule 10 {
            action accept
            description "Allow established/related sessions"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    ipv6-name WANv6_LOCAL {
        default-action drop
        description "WAN inbound traffic to the router"
        enable-default-log
        rule 10 {
            action accept
            description "Allow established/related sessions"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
        rule 30 {
            action accept
            description "Allow IPv6 icmp"
            protocol ipv6-icmp
        }
        rule 40 {
            action accept
            description "allow dhcpv6"
            destination {
                port 546
            }
            protocol udp
            source {
                port 547
            }
        }
    }
    name WAN_IN {
        default-action drop
        description "WAN to internal"
        rule 10 {
            action accept
            state {
                established enable
                related enable
            }
            description "Allow established/related"
        }
        rule 20 {
            action drop
            state {
                invalid enable
            }
            description "Drop invalid state"
        }
    }
    name WAN_LOCAL {
        default-action drop
        description "WAN to router"
        rule 10 {
            action accept
            state {
                established enable
                related enable
            }
            description "Allow established/related"
        }
        rule 20 {
            action drop
            state {
                invalid enable
            }
            description "Drop invalid state"
        }
    }
    options {
        mss-clamp {
            mss 1412
        }
    }
    receive-redirects disable
    send-redirects enable
    source-validation disable
    syn-cookies enable
}
interfaces {
    ethernet eth0 {
        speed auto
        duplex auto
        vif 6 {
        description "Internet (PPPoE)"
        pppoe 0 {
            default-route auto
            dhcpv6-pd {
                pd 0 {
                    interface switch0 {
                        host-address ::1
                        prefix-id :1
                        service slaac
                    }
                    prefix-length /48
                }
                rapid-commit enable
            }
            firewall {
                in {
                    ipv6-name WANv6_IN
                    name WAN_IN
                }
                local {
                    ipv6-name WANv6_LOCAL
                    name WAN_LOCAL
                }
            }
            mtu 1492
            name-server auto
            password 1234
            user-id fake@freedom.nl
        }
        }
    }
    ethernet eth1 {
        description Local
        duplex auto
        speed auto
    }
    ethernet eth2 {
        description Local
        duplex auto
        speed auto
    }
    ethernet eth3 {
    	description Local
        duplex auto
        speed auto
    }
    ethernet eth4 {
    	description Local
        duplex auto
        speed auto
    }
    ethernet eth5 {
        speed auto
        duplex auto
    }
    loopback lo {
    }
    switch switch0 {
        address 10.0.0.1/24
        description Local
        switch-port {
            interface eth1
            interface eth2
            interface eth3
            interface eth4
        }
    }
}
service {
    dhcp-server {
        disabled false
        hostfile-update disable
        shared-network-name LAN {
            authoritative enable
            subnet 10.0.0.0/24 {
                default-router 10.0.0.1
                dns-server 10.0.0.1
                lease 86400
                start 10.0.0.38 {
                    stop 10.0.0.243
                }
            }
        }

    }
    dns {
        forwarding {
            cache-size 150
            listen-on switch0
        }
    }
    gui {
        https-port 443
    }
    nat {
        rule 5010 {
            outbound-interface pppoe0
            type masquerade
            description "masquerade for WAN"
        }
    }
    ssh {
        port 22
        protocol-version v2
    }
}
system {
    host-name ubnt
    login {
        user CHANGEME {
            authentication {
                CHANGEME}
            level admin
        }
    }
    ntp {
        server 0.ubnt.pool.ntp.org {
        }
        server 1.ubnt.pool.ntp.org {
        }
        server 2.ubnt.pool.ntp.org {
        }
        server 3.ubnt.pool.ntp.org {
        }
    }
    syslog {
        global {
            facility all {
                level notice
            }
            facility protocols {
                level debug
            }
        }
    }
    time-zone UTC
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@5:nat@3:qos@1:quagga@2:suspend@1:system@4:ubnt-pptp@1:ubnt-udapi-server@1:ubnt-unms@1:ubnt-util@1:vrrp@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v1.10.11.5274269.200221.1028 */
